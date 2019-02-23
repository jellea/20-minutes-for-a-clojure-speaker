(ns minutes-for-a-speaker.localstorage
  (:require [reagent.core :as r]
            [goog.dom :as gdom]
            [re-frame.core :as rf]
            [cognitect.transit :as t]))

(rf/reg-sub
  :raw-db
  (fn [db _]
    db))

(rf/reg-sub
  :page
  (fn [db _]
    (:page db)))

(rf/reg-event-db
  :reset
  (fn [db]
    {:page 0}))

(rf/reg-event-db
  :inc-counter
  (fn [db]
    (update db :page inc)))

(rf/reg-event-db
  :next-page
  (fn [db]
    (update db :page inc)))

(rf/reg-event-db
  :previous-page
  (fn [db]
    (update db :page dec)))

(defn set-localstorage! [s]
  (js/localStorage.setItem "re-frame-action" s))

(def reader (t/reader :json))
(def writer (t/writer :json))

(defn transit-parse [s]
  (t/read reader s))

(defn transit-stringify [o]
  (t/write writer o))

(defn get-localstorage! []
  (->> (js/localStorage.getItem "re-frame-action")
       transit-parse))

(defn write-to-localstorage! [e]
  (-> (get-localstorage!)
      (conj e)
      (vec)
      transit-stringify
      (set-localstorage!)))

(defn localstorage->re-frame-actions! []
  (doseq [a (get-localstorage!)]
    (rf/dispatch a)))

(defn dispatch-on-new-value [event]
  (some-> (.-newValue event) transit-parse last rf/dispatch))

(defn set-localstorage-listener! []
  (js/addEventListener "storage"
    dispatch-on-new-value
    false))

(defn remove-localstorage-listener! []
  (js/removeEventListener "storage" dispatch-on-new-value))
