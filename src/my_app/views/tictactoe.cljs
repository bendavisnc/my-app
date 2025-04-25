(ns my-app.views.tictactoe
  (:require 
   [my-app.pieces.tictactoe :as pieces]
   [my-app.models.tictactoe :as models]
   [clojure.spec.alpha :as s]
   [cljs.math :as math]
   [re-frame.core :as re-frame]))

(defn square [& {:keys [index, piece]}]
  (let [class ({:x "x" :o "o" :empty "empty"} piece)]
    [:button.square {:class class
                     :on-click #(re-frame/dispatch [::models/on-square-select index])}
     piece]))

(defn board [squares]
  [:div
   (let [squares-count (count squares)]
    (for [row (partition (math/sqrt squares-count) (range squares-count))]
        ^{:key row}
        [:div.board-row (for [i row]
                         ^{:key i} [square :index i :piece (get squares i)])]))])

(defn jump-to-button [index]
  (let [description (if (zero? index)
                      "Go to game start"
                     (str "Go to move #" index))]
    [:button {:on-click #(re-frame/dispatch [:on-jump-to-button-click index])}
      description]))

(defn game-info [& {:keys [history, status]}]
  [:div.game-info
    [:div.status status]
    [:div.history [:ol (for [i (range (count history))]
                        ^{:key i}
                        [:li [jump-to-button i]])]]])

(defn tictactoe-component [model]
  [:div.game
   [:div.game-board [board (:squares model)]]
   [game-info :history (:history model)
              :status (:status model)]])     

  
;; (s/fdef tictactoe-component
;;   :args (s/cat :model ::spec/component))

(defn tictactoe []
  (let [model (re-frame/subscribe [::models/tictactoe])]
    (fn []
      (println @model)
      [tictactoe-component @model])))


