(ns my-app.core
  (:require
   [reagent.core :as reagent :refer [atom]]
   [reagent.dom :as rdom]))

(def x "✘")
(def o "⭕")

;; Returns a reagent hiccup component that represents one square on the board.
(defn square [on-click, squares, i]
  [:button {:class-name "square"
            :on-click #(on-click i)}
   (get squares i)])

;; Returns a reagent hiccup component that holds all nine squares.
(defn board [on-click, squares]
  [:div
   [:div {:class-name "board-row"}
    [square on-click, squares, 0]
    [square on-click, squares, 1]
    [square on-click, squares, 2]]
   [:div {:class-name "board-row"}
    [square on-click, squares, 3]
    [square on-click, squares, 4]
    [square on-click, squares, 5]]
   [:div {:class-name "board-row"}
    [square on-click, squares, 6]
    [square on-click, squares, 7]
    [square on-click, squares, 8]]])

;; Takes a tic-tac-toe board in the form of a list of player symbols and compares it with each possible set of winning positions.
;; Returns a non nil string character representing the winner, if there is one.
(defn winner-check [squares]
  (let [lines [[0 1 2] [3 4 5] [6 7 8]
               [0 3 6] [1 4 7] [2 5 8]
               [0 4 8] [2 4 6]]]
    (some (fn [line]
            (let [[a b c] (map squares line)]
              (when (and a (= a b c))
                a)))
          lines)))

;; Provides the board's squares' `on-click` function.
;; Updates global game state. 
;; Invoked with the square's index itself whenever an individual square is clicked.
;; See `board` and `game-fn`.
(defn handle-click-fn [gs]
  (fn [i]
    (let [{:keys [history step-number is-x-next?]} @gs
          history* (subvec history 0 (inc step-number))
          {:keys [squares]} (last history*)
          square-occupied? (get squares i)
          winner (winner-check squares)
          should-update-state? (and (not square-occupied?) (not winner))]
      (when should-update-state?
        (swap! gs
               (fn [gs]
                 (-> gs
                     (update :history conj {:squares (assoc squares i (if is-x-next? x o))})
                     (assoc :step-number (count history*))
                     (update :is-x-next? not))))))))

;; Returns state to represent the playing of a tic-tac-toe game.
(def start-game-state
  (let [tictactoe-squares-count 9]
    {:history [{:squares (vec (repeat tictactoe-squares-count
                                      nil))}]
     :step-number 0
     :is-x-next? true}))

;; Updates game state to a requested `move-index`.
(defn jump-to! [gs, move-index]
  (swap! gs (fn [gs]
              (-> gs
                  (assoc :step-number move-index)
                  (assoc :is-x-next?
                         (zero? (mod move-index 2)))))))

;; Returns button for resetting game state to previously made move.
(defn jump-to-button [gs, move-index, desc]
  [:button {:on-click (fn []
                        (jump-to! gs move-index))}
   desc])

;; Provides move history ui.
(defn game-info [gs, history, status]
  [:div {:class-name "game-info"}
   [:div status]
   [:ol
    (for [i (range (count history))
          :let [desc (if (zero? i)
                       "Go to game start"
                       (str "Go to move #" i))]]
      [:li {:key i} [jump-to-button gs i desc]])]])

;; Provides reagent hiccup component with a state atom for the game.
(defn game-fn []
  (let [gs (atom start-game-state)]
    (fn []
      (let [{:keys [step-number history is-x-next?]} @gs
            {:keys [squares]} (get history step-number)
            winner (winner-check squares)
            status (if winner
                     (str "Winner: " winner)
                     (str "Next player: " (if is-x-next? x o)))]
        [:div {:class-name "game"}
         [:div {:class-name "game-board"}
          [board (handle-click-fn gs) squares]]
         [game-info gs history status]]))))

;; Calls render with root hiccup component and js app container element.
(defn mount-root []
  (rdom/render [(game-fn)] (.getElementById js/document "app")))

;; Provides execution starting point.
(defn init! []
  (mount-root))
