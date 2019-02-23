(ns ^:figwheel-hooks minutes-for-a-speaker.core
  (:require
   [goog.dom :as gdom]
   [minutes-for-a-speaker.specs :as spcs]
   [minutes-for-a-speaker.views.presenter]
   [minutes-for-a-speaker.views.audience]
   [reagent.core :as reagent :refer [atom]]))

(defn start []
 (if (= js/window.view "presenter")
  (minutes-for-a-speaker.views.presenter/mount-app-element)
  (minutes-for-a-speaker.views.audience/mount-app-element)))

(start)

;; specify reload hook with ^;after-load metadata
;(defn ^:after-load on-reload []
;  (start))

 ;; optionally touch your app-state to force rerendering depending on
 ;; your application
 ;; (swap! app-state update-in [:__figwheel_counter] inc)

