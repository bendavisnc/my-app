(ns my-app.core
  (:require
   [reagent.core :as reagent :refer [atom]]
   [reagent.dom :as rdom]))

(defn square [on-click, text, id]
  [:button {:class-name "square"
            :id id
            :on-click on-click}
   text])

(defn square-with-index [on-click, squares, i]
  [square (fn []
            (on-click i))
   (get squares i)
   i])

(defn board [on-click, squares]
  [:div
   [:div {:class-name "board-row"}
    [square-with-index on-click, squares, 0]
    [square-with-index on-click, squares, 1]
    [square-with-index on-click, squares, 2]]
   [:div {:class-name "board-row"}
    [square-with-index on-click, squares, 3]
    [square-with-index on-click, squares, 4]
    [square-with-index on-click, squares, 5]]
   [:div {:class-name "board-row"}
    [square-with-index on-click, squares, 6]
    [square-with-index on-click, squares, 7]
    [square-with-index on-click, squares, 8]]])

(defn winner-check [squares]
  (let [lines
        [[0, 1, 2]
         [3, 4, 5]
         [6, 7, 8]
         [0, 3, 6]
         [1, 4, 7]
         [2, 5, 8]
         [0, 4, 8]
         [2, 4, 6]]]
    (first (for [line lines
                 :let [s
                       (set (map (partial get squares)
                                 line))]
                 :when (= 1 (count s))
                 winner s
                 :when (not (nil? winner))]
             winner))))

(defn handle-click-fn [gs]
  (fn [i]
    (let [history (:history (deref gs))
          step-number (:step-number (deref gs))
          history* (subvec history
                           0
                           (inc step-number))
          current (last history*)
          squares (:squares current)
          is-square-occupied? (get squares i)
          winner (winner-check squares)
          should-update-state? (not (or is-square-occupied?
                                        winner))]
      (if should-update-state?
        (swap! gs
               (fn [gs]
                 (-> gs
                     (assoc :history (conj history*
                                           {:squares (assoc squares
                                                            i
                                                            (if (:is-x-next? gs)
                                                              "X"
                                                              "0"))}))
                     (assoc :step-number (count history*))
                     (update :is-x-next? not))))
        nil))))

(def start-game-state
  {:history [{:squares [nil,nil,nil,nil,nil,nil,nil,nil,nil]}]
   :step-number 0
   :is-x-next? true})

(defn jump-to! [gs, move-index]
  (swap! gs (fn [gs]
              (-> gs
                  (assoc :step-number move-index)
                  (assoc :is-x-next?
                         (zero? (mod move-index 2)))))))

(defn jump-to-button [gs, move-index, desc]
  [:button {:on-click (fn []
                        (jump-to! gs move-index))}
   desc])

(defn game-info [gs, history, status]
  [:div {:class-name "game-info"}
   [:div status]
   [:ol
    (for [i (range (count history))]
      (let [desc (if (zero? i)
                   "Go to game start"
                   (str "Go to move #" i))]
        [:li {:key i} [jump-to-button gs i desc]]))]])


(defn game []
  (let [gs (atom start-game-state)]
    (fn []
      (let [game-state (deref gs)
            step-number (:step-number  game-state)
            current (get (:history   game-state)
                         step-number)
            winner (winner-check (:squares current))
            is-x-next? (:is-x-next?  game-state)
            status (if winner
                     (str "Winner: " winner)
                     (str "Next player: " (if is-x-next? "X" "O")))]
        [:div {:class-name "game"}
         [:div {:class-name "game-board"}]
         [board (handle-click-fn gs)
          (:squares current)]
         [game-info gs (:history game-state)
          status]]))))


(defn mount-root []
  (rdom/render [(game)] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
