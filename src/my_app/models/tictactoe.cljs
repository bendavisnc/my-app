(ns my-app.models.tictactoe
  (:require
   [my-app.pieces.tictactoe :as pieces]
   [re-frame.core :as re-frame]))

(def squares-count 9)

(defn winner-check [squares]
  (let [lines [[0 1 2] [3 4 5] [6 7 8]
               [0 3 6] [1 4 7] [2 5 8]
               [0 4 8] [2 4 6]]]
    (some (fn [line]
            (let [[a b c] (map squares line)]
              (when (and a (= a b c)) a)))
          lines)))

(defn apply-new-move [squares, index, is-x-next?, winner]
  (let [square-occupied? (squares index)]
    (when-not (or winner 
                  square-occupied?)
      (assoc squares index (if is-x-next? pieces/x pieces/o)))))

(re-frame/reg-event-db
 :initialize
 (fn [_ _]
   {:step-number 0
    :history [{:squares (vec (repeat squares-count nil))}]
    :is-x-next? true}))

(re-frame/reg-event-db
 ::on-square-select
 (fn [db [_ index]]
   (if-let [new-move 
            (apply-new-move (get-in db [:history (:step-number db) :squares]) 
                            index 
                            (:is-x-next? db)
                            (:winner db))]
     (let [new-history (conj (vec (take (inc (:step-number db)) (:history db)))
                             {:squares new-move})]
       (-> db
           (assoc :history new-history)
           (assoc :step-number (dec (count new-history)))
           (assoc :is-x-next? (not (:is-x-next? db)))
           (assoc :winner (winner-check new-move))))
     db)))

(re-frame/reg-event-db
 ::on-move-history-select
 (fn [db [_, index]]
   (-> db
       (assoc :step-number index)
       (assoc :is-x-next? (zero? (mod index 2))))))

(re-frame/reg-sub
 ::is-x-next?
 (fn [db, _]
   (:is-x-next? db)))

(re-frame/reg-sub
 ::history
 (fn [db, _]
   (:history db)))

(re-frame/reg-sub
 ::step-number
 (fn [db, _]
   (:step-number db)))

(re-frame/reg-sub
 ::history-effective
 (fn [_, _]
   [(re-frame/subscribe [::history]),
    (re-frame/subscribe [::step-number])])
 (fn [[history, step-number], _]
   (subvec history 0 (inc step-number))))

(re-frame/reg-sub
 ::squares
 (fn [_, _]
   [(re-frame/subscribe [::history-effective])])
 (fn [[history-effective], _]
   (:squares (last history-effective))))

(re-frame/reg-sub
 ::status
 (fn [_, _]
   [(re-frame/subscribe [::is-x-next?]),
    (re-frame/subscribe [::winner])])
 (fn [[is-x-next?, winner], _]
   (if winner
     (str "Winner: " winner)
     ;; else
     (str "Next player: " (if is-x-next? pieces/x pieces/o)))))

(re-frame/reg-sub
 ::winner
 (fn []
   [(re-frame/subscribe [::squares]), (re-frame/subscribe [::is-x-next?])])
 (fn [[squares, is-x-next?]]
   (when-let [_ (winner-check squares)]
     (let [last-piece (if is-x-next? pieces/o pieces/x)]
       last-piece))))

(re-frame/reg-sub
 ::tictactoe
 (fn []
   [(re-frame/subscribe [::squares]),
    (re-frame/subscribe [::winner]),
    (re-frame/subscribe [::history]),
    (re-frame/subscribe [::status])])
 (fn [[squares, winner, history, status]]
   {:squares squares
    :winner winner
    :history history
    :status status}))