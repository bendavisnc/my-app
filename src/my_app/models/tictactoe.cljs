(ns my-app.models.tictactoe
  (:require [re-frame.core :as re-frame]))

(def squares-count 9)

(defn winner-check [squares]
  (let [lines [[0 1 2] [3 4 5] [6 7 8]
               [0 3 6] [1 4 7] [2 5 8]
               [0 4 8] [2 4 6]]]
    (some (fn [line]
            (let [[a b c] (map squares line)]
              (when (and a (= a b c)) a)))
          lines)))

(re-frame/reg-event-db
 :initialize
 (fn [_ _]
   {:step-number 0
    :history [{:squares (vec (repeat squares-count nil))}]
    :is-x-next? true}))

(re-frame/reg-sub
 ::squares
 (fn [db [_ _]]
   (:squares (last (:history db)))))

(re-frame/reg-sub
 ::tictactoe
 (fn []
   [(re-frame/subscribe [::squares])])
 (fn [[squares]]
   {:squares squares}))