(ns my-app.view
  (:require [my-app.views.tictactoe :as tictactoe]))

(defn ui []
  [tictactoe/tictactoe])
;;   [:div.game
;;    [:div.game-board [tictactoe/board]]
;;    [tictactoe/game-info]])
