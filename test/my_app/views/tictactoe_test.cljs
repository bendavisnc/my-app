(ns my-app.views.tictactoe-test
  (:require
   [devcards.core :refer-macros [defcard-rg]]
   [my-app.pieces.tictactoe :as pieces]
   [my-app.views.tictactoe :as views]))

(defcard-rg square
  [:div
   [:h1 [:i "square"]]
   [views/square :index 0 :piece pieces/x]])


(defcard-rg board
  [:div
   [:h1 [:i "board"]]
   [views/board (vec (repeat 9 pieces/x))]])


(defcard-rg jump-to-button
  [:div
   [:h1 [:i "jump-to-button, zero"]]
   [views/jump-to-button 0]
   [:h1 [:i "jump-to-button"]]
   [views/jump-to-button 1]])







