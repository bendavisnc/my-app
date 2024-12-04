(ns my-app.core
  (:require 
    [reagent.dom.client :as rdc]
    [re-frame.core :as rf]))

(enable-console-print!)

(def x "✘")
(def o "⭕")
(def squares-count 9)

;; Utility functions
(defn winner-check [squares]
  (let [lines [[0 1 2] [3 4 5] [6 7 8]
               [0 3 6] [1 4 7] [2 5 8]
               [0 4 8] [2 4 6]]]
    (some (fn [line]
            (let [[a b c] (map squares line)]
              (when (and a (= a b c)) a)))
          lines)))

;; Event handlers
(rf/reg-event-db
  :initialize
  (fn [_ _]
    {:step-number 0
     :history [{:squares (vec (repeat squares-count nil))}]
     :is-x-next? true}))

(rf/reg-event-db
  :square-clicked
  (fn [db [_ i]]
    (let [{:keys [history step-number is-x-next?]} db
          history* (subvec history 0 (inc step-number))
          {:keys [squares]} (last history*)
          winner @(rf/subscribe [:winner])
          square-occupied? (squares i)
          can-update? (not (or square-occupied? winner))]
      (if-not can-update?
        db
        (-> db
            (assoc :history
                   (conj history*
                         {:squares (assoc squares i (if is-x-next? x o))}))
            (assoc :step-number (count history*))
            (assoc :winner winner)
            (update :is-x-next? not))))))

(rf/reg-event-db
  :history-jump
  (fn [db [_ i]]
    (-> db
        (assoc :step-number i)
        (assoc :is-x-next? (zero? (mod i 2))))))

;; Subscriptions
(rf/reg-sub
  :square
  (fn [db [_ i]]
    (get-in db [:history (:step-number db) :squares i])))

(rf/reg-sub
  :history
  (fn [db _] (:history db)))

(rf/reg-sub
  :is-x-next?
  (fn [db _] (:is-x-next? db)))

(rf/reg-sub
  :winner
  (fn [db _]
    (let [{:keys [history step-number]} db
          {:keys [squares]} (last (subvec history 0 (inc step-number)))]
      (winner-check squares))))

(rf/reg-sub
  :status
  (fn [_, _]
    [(rf/subscribe [:is-x-next?]),
     (rf/subscribe [:winner])])
  (fn [[is-x-next?, winner] _]
    (if winner
      (str "Winner: " winner)
      ;; else
      (str "Next player: " (if is-x-next? x o)))))

;; UI components
(defn square [i]
  (let [v @(rf/subscribe [:square i])
        is-x? (= x v)]
    [:button.square {:class (if is-x? "x" "o") 
                     :on-click #(rf/dispatch [:square-clicked i])}
     v]))

(defn board []
  [:div
   (for [row (partition 3 (range squares-count))]
     ^{:key row}
     [:div.board-row (for [i row]
                       ^{:key i} [square i])])])

(defn jump-to-button [move-index desc]
  [:button {:on-click #(rf/dispatch [:history-jump move-index])}
   desc])

(defn game-info []
  (let [history @(rf/subscribe [:history])
        status @(rf/subscribe [:status])]
    [:div.game-info
     [:div.status status]
     [:div.history [:ol (for [i (range (count history))]
                          ^{:key i}
                          [:li [jump-to-button i
                                 (if (zero? i)
                                   "Go to game start"
                                   (str "Go to move #" i))]])]]]))

(defn ui []
  [:div.game
   [:div.game-board [board]]
   [game-info]])

(defonce app-root
  (rdc/create-root (js/document.getElementById "app")))

(defn mount-ui []
  (rdc/render app-root [ui]))

(defn run []
  (rf/dispatch-sync [:initialize])
  (mount-ui))

(run)
