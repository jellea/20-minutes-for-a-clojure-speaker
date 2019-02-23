(ns minutes-for-a-speaker.views.audience
  (:require [reagent.core :as r]
            [goog.dom :as gdom]
            [re-frame.core :as rf]
            [minutes-for-a-speaker.localstorage :as ls]
            [minutes-for-a-speaker.styling :as stl]
            [minutes-for-a-speaker.progress :as pr]))

(defn loader-spinner []
  [:div.loader
   [:div.loader-inner.ball-grid-pulse ;.ball-grid-beat
    [:div]
    [:div]
    [:div]
    [:div]
    [:div]
    [:div]
    [:div]
    [:div]
    [:div]]])

(rf/reg-sub
  :counter
  (fn [db _]
    (:page db)))

(defn pres-ui []
  [:div.page
   ;[loader-spinner]
   ;[:p (str @(rf/subscribe [:raw-db]))]
   (pr/page @(rf/subscribe [:counter]))])

(defn scaffold []
  (r/with-let [_ (ls/localstorage->re-frame-actions!)
               _ (ls/set-localstorage-listener!)]
    [pres-ui]
   (finally
     ;(ls/remove-localstorage-listener!)
     (rf/remove-post-event-callback :sink))))

(defn mount [el]
  (r/render-component [scaffold] el))

(defn mount-app-element []
  (when-let [el (gdom/getElement "app")]
    (mount el)))
