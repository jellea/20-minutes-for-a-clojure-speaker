(ns minutes-for-a-speaker.progress
  (:require [re-frame.core :as rf]
            [reagent.core :as r]))

(rf/reg-sub :talk/title
   (fn [db _]
     (some-> db :talk :title)))

(rf/reg-sub :talk/slide
  (fn [db [_ num]]
    (some-> db :talk :slides (nth (+ num -3) nil))))

(rf/reg-sub :talk/style
  (fn [db [_ num]]
    (some-> db :talk :slide-styles (nth (+ num -3) nil))))

(defmulti page (fn [n] n))

(defmethod page 1 [_]
  [:div.blue.serif
   [:h1 "📣 PSA"]
   [:h1 "Don't worry this talk will be short and it won't be recorded."]])

(defmethod page 2 [_]
  [:div.yellow.grotesque
   [:h1 "Hi, I'm Jelle"]
   [:h1 "and you should take everything I say today with a grain of salt."]])

(defmethod page 3 [_]
  (let [title @(rf/subscribe [:talk/title])]
    [:div.green.serif
     [:h1 "This talk's title is"]
     (if title
       [:h1 (str "“" title "”")]
       [:h1 "..."])]))

(defmethod page 14 [_]
  [:div.blue.serif
   [:h1 "Did you figure the talk out yet?"]])

;(defmethod page 15 [_]
;  [:div.green.grotesque
;   [:h1 ""]])

(defmethod page 21 [_]
  [:div.green.serif
   [:h1 "Conclusion"]])

(defmethod page 22 [_]
  [:div
   [:h1 "Questions?"]])

(defmethod page 23 [_]
  [:div.yellow.grotesque
   [:h1 "FIN"]])

(defmethod page 24 [_]
  [:div.blue.grotesque
   [:h1 "Credits"]
   [:p "https://juxt.pro/blog/posts/generative-ui-clojure-spec.html" [:br]
    "http://conan.is/blogging/a-spec-for-urls-in-clojure.html" [:br]
    "https://codesai.com/2018/03/kata-generating-bingo-cards" [:br]
    "http://upgradingdave.com/blog/posts/2016-06-21-random-pwd-with-spec.html" [:br]
    "http://arnebrasseur.net/talks/2016-clojutre/" [:br]
    "https://blog.michielborkent.nl/2017/10/10/parsing-a-circuit-with-clojure-spec/" [:br]
    "https://www.bradcypert.com/an-informal-guide-to-clojure-spec/" [:br]
    "https://blog.jeaye.com/2017/05/31/clojure-spec/" [:br]
    "https://adambard.com/blog/domain-modeling-with-clojure-spec/"]])

(defmethod page 25 [_]
  [:div.yellow.mono
   [:h1 "Generate your own talk!"]
   [:h1 "https://github.com/jellea"]
   [:h1 "/20-minutes-for-a-clojure-speaker"]])

(defmethod page 0 [_]
  [page nil])

(defmethod page nil [_]
  [:div.flex-center
   [:img {:src "/jc.jpg" :style {:mix-blend-mode "luminosity"}}]]);}}]])

(defn code-slide []
  (r/create-class
    {:component-did-mount #(js/Prism.highlightAll)
     :component-did-update #(js/Prism.highlightAll)
     :reagent-render
     (fn [{:keys [code]} style]
       (let [{:keys [bg-colour font]} (first style)]
         [:div {:class [font bg-colour]}
          [:pre.coder [:code.lang-clojure code]]]))}))

(defn quote-slide [{:keys [quote]} style]
  (let [{:keys [bg-colour font]} (first style)]
    [:div {:class [font bg-colour]}
     [:h2 (str (:txt quote))]
     [:h2 (str " – " (:author quote))]]))

(defmethod page :default [num]
  (let [slide @(rf/subscribe [:talk/slide num])
        style [@(rf/subscribe [:talk/style num])]]
    (case (some-> slide first key)
      :code [code-slide slide style]
      :quote [quote-slide slide style]
      nil [:div.green.serif
           [:h1 "slide intentionally left blank"]]
      [:div
       [:p (str slide)]])))

