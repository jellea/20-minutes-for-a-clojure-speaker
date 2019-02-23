(ns minutes-for-a-speaker.views.presenter
  (:require
    [goog.dom :as gdom]
    [minutes-for-a-speaker.specs :as spcs]
    [reagent.core :as r :refer [atom]]
    [re-frame.core :as rf]
    [minutes-for-a-speaker.views.audience :as audience]
    [minutes-for-a-speaker.localstorage :as ls]
    [goog.string :as gs]
    [goog.string.format]))

;(defn pp-ui []
;  [:pre (some-> @app-state :talk cljs.pprint/pprint with-out-str)])

(defn pretty-talk [talk]
  (when talk
    (into
      [:div.lines]
      (map (fn [l]
            [:p l])
           (:speech talk)))))

(defonce !interval-id (atom nil))
(defonce !counter (r/atom 0))
(defonce !counter-status (r/atom :stopped))

(defn counter-click! [ok]
  (case (or ok @!counter-status)
    :stopped (do (reset! !counter-status :running)
                 (reset! !counter 0)
                 (js/clearInterval @!interval-id)
                 (reset! !interval-id (js/setInterval #(swap! !counter inc) 1000)))
    :running (do (reset! !counter-status :stopped)
                 (js/clearInterval @!interval-id))))

(rf/reg-sub :gests
  (fn [db _]
    (when-let [talk (:talk db)]
      (concat (some->> talk :next-slides (map (fn [t] {:non-verbal "next slide" :timestamp t})))
              (some-> talk :gestures)))))

(defn gest [{:keys [timestamp non-verbal]}]
  (let [ns (= non-verbal "next slide")]
    [:p.time {:style {:top (* timestamp 35)
                      :color (when ns "red")
                      :left (if ns 200 0)}} (str non-verbal)]))

(defn gestures []
  (let [gestures-and-next-slides @(rf/subscribe [:gests])]
    (when gestures-and-next-slides
      (into [:<>]
            (map (fn [g]
                   [gest g])
                 gestures-and-next-slides)))))

(defn teleprompter []
    (r/create-class
      {:reagent-render
       (fn []
         (let [page-no @(rf/subscribe [:page])]
           [:div.teleprompter {:on-click #(counter-click! nil)}
            [:p {:style {:font-size 18}}
             (str (gs/format "%02d" (js/Math.floor (/ @!counter 60)))
                  ":" (gs/format "%02d" (mod @!counter 60))
                  " " (name @!counter-status)
                  " - " page-no "/25")]
            [:div.tele.outer
             [:div.inner {:style {:transform (str "translateY(-" (* @!counter 35) "px)")}}
              [gestures]]]]))}))



(defn ui []
  (let [db @(rf/subscribe [:raw-db])]
    [:div
     [:div.right
      [:div {:style {:width 588 :height 336}}
       [:div.audience-view
        [audience/pres-ui]]]
      [teleprompter]]
     ; [:p page-no]
     [:div.presenter-view
       ;[:button {:on-click #(rf/dispatch [:inc-counter])} "next page"]
       [:button {:on-click #(do (js/localStorage.removeItem "re-frame-action")
                                (rf/dispatch [:reset]))}
        "reset"]
       [:button {:on-click #(do (rf/dispatch [:generate (spcs/generate-talk)])
                                (counter-click! :stopped))}
        "generate"]]
       ;[:p (str db)]]
     [pretty-talk (:talk db)]]))

(defn register-keyboard-events! []
  ;(js/Mousetrap.bind "space" #(rf/dispatch [:inc-counter]))
  (js/Mousetrap.bind "left" #(rf/dispatch [:previous-page]))
  (js/Mousetrap.bind "right" #(rf/dispatch [:next-page]))
  (js/Mousetrap.bind "r e s e t" #(rf/dispatch [:reset])))

(rf/reg-event-db :tick-timer
  (fn [db _]
    (update db :timer inc)))

(defn once-wrapper []
  (r/create-class
    {:component-did-mount (fn []
                            ;(ls/localstorage->re-frame-actions!)
                            (rf/add-post-event-callback :sink ls/write-to-localstorage!)
                            (register-keyboard-events!))
                            ;(js/setInterval #(rf/dispatch [:tick-timer] 1)))
     :component-will-unmount (fn [])

     :display-name "ls-wrapper"
     :reagent-render
     (fn []
       [:div
        [ui]])}))

(defn mount [el]
  (r/render-component [once-wrapper] el))

(defn mount-app-element []
  (when-let [el (gdom/getElement "app")]
    (mount el)))
