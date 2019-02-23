(ns minutes-for-a-speaker.specs
  (:require [clojure.spec.alpha :as s]
            [clojure.test.check.generators]
            [clojure.spec.gen.alpha :as gen]
            [re-frame.core :as rf]
            [clojure.string :as st]
            [minutes-for-a-speaker.content :as content]
            [clojure.string :as str]))

(defonce !chain (atom {}))

;; {"BEGIN" {"THE" 1}}

(defn add-to-chain [c sentence]
  (assert (string? sentence) "Not a string!")
  (let [words (-> (str "BEGIN " (str/trim sentence) " END")
                  (st/split #" ")) ;; TODO: regex one or more white-space.
        pairs (interleave (partition 2 words)
                          (partition-all 2 (rest words)))]
    (reduce (fn [acc [h t]] (update-in acc [h t] inc)) c pairs)))

(def articles (->> content/articles flatten (keep :speech) set))
(def codes (->> content/articles flatten (keep :code) set))
(def quotes (->> content/articles flatten (keep :quote) set))

(s/def ::code (set codes))
(s/def ::quote (set quotes))

(comment
  codes
  quotes)

(defonce add-to-set!
  (doseq [s articles]
    (swap! !chain add-to-chain s)))

(comment
  (doseq [s articles]
    (swap! !chain add-to-chain s))

  (add-to-chain {} "The cat")
  (add-to-chain {} "The cat walks")

  @!chain
  (swap! !chain add-to-chain "The cat walks to the big tree.")
  (swap! !chain add-to-chain "The dog has a big smile."))

(defn chain
  "Returns a lazy Markov chain starting with 'current' using matrix 'trans'"
  [current trans]
  (if-let [transitions (trans current)]
    (cons (first current)
          (lazy-seq (chain (concat (rest current) [(rand-nth transitions)]) trans)))
    current))

(defn weighted-rand-choice [m seed]
  (assert (map? m) "Not a map!")
  (let [w (reductions #(+ % %2) (vals m))
        r (rand-int (last w))]
    (nth (keys m) (count (take-while #( <= % r ) w)))))

(defn generate-sentence* [start chain seed]
  (if-let [transition (get chain start)]
    (cons start
          (lazy-seq (generate-sentence*
                      (weighted-rand-choice transition seed)
                      chain seed)))))

(defn generate-sentence [start chain seed]
  (->> (generate-sentence* start chain seed)
       rest
       butlast
       (str/join " ")))

(comment (generate-sentence* "BEGIN" @!chain 2))

(s/def ::real-sentence (set articles))

(defn gen-markov-sentence []
  (gen/fmap (fn [i] (generate-sentence "BEGIN" @!chain i))
            (gen/int)))

(s/def ::markov-sentence
  (s/with-gen
    (s/and string? #(< (count %) 150))
    gen-markov-sentence))

(s/def ::sentence
  (s/with-gen
    (s/and string? #(< (count %) 150))
    #(gen/frequency [[8 (s/gen ::markov-sentence)] [1 (s/gen ::real-sentence)]])))

(comment
  (gen/generate (s/gen ::sentence)))

(s/def ::short-sentence
  (s/with-gen
    (s/and string? #(< (count %) 50) #(not (st/blank? %)))
    gen-markov-sentence))

(comment
  (gen/generate (s/gen ::short-sentence)))

(s/def ::title ::short-sentence)

(s/def ::non-verbal #{"snore" "lean on elbow" "drag presenter to screen" "hiss" "slap table" "cough" "brush hair" "blow nose" "laugh" "clap"
                      "hold up hand" "gargle" "silence for 10 seconds" "rub eyes" "bang fist on table" "yawn" "touch nose and ears" "click"
                      "hold up watch [to mic]" "kiss sound" "whistle three times" "light lighter"})

(s/def ::timestamp (s/int-in 30 900))

(s/def ::gesture-in-time (s/keys :req-un [::non-verbal ::timestamp]))

(s/def ::gestures (s/and (s/coll-of ::gesture-in-time
                                    :kind vector? :count 40)
                         #(->> (map :timestamp %)
                               (apply distinct?))))

(s/def ::next-slides (s/and (s/coll-of ::timestamp :distinct true :count 25)))

(s/def ::font #{"grotesque" "serif" "mono"})
(s/def ::bg-colour #{"" "green" "yellow"})

(s/def ::slide (s/or :code (s/keys :req-un [::code])
                     :quote (s/keys :req-un [::quote])))

(s/def ::slides (s/coll-of ::slide :distinct true :count 18))

(comment
  (gen/generate (s/gen ::slides)))

(s/def ::slide-style (s/keys :req-un [::font ::bg-colour]))
(s/def ::slide-styles (s/coll-of ::slide-style :count 18))

(comment
  (gen/generate (s/gen ::slide-styles)))

(s/def ::speech (s/coll-of ::sentence :kind vector? :distinct true :count 200))
(s/def ::talk (s/keys :req-un [::title ::speech ::gestures ::next-slides
                               ::slides ::slide-styles]))

(defn generate-talk []
  (-> (s/gen ::talk)
      gen/generate))

(rf/reg-event-db :generate
  (fn [db [_ talk]]
    (assoc db :talk talk)))

(comment
  (generate-talk))
