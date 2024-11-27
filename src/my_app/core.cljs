(ns my-app.core
    (:require 
      [reagent.dom.client :as rdc]
      [re-frame.core :as rf]))


(enable-console-print!)

(def x "✘")
(def o "⭕")

;; event handlers

(rf/reg-event-db
  :initialize
  (fn [_ _]
    (let [squares-how-many 9]
      {:squares (vec (repeat squares-how-many
                            nil))})))

(rf/reg-event-db
  :square-clicked            
  (fn [db [_ i]]
    (println ["event handler", db, i])
    db))

(rf/reg-sub
  :square
  (fn [db [_, i]]
    (println ["subscription handler", db, i])
    (get-in db [:squares i])))

(defn square [i]
  (let [v @(rf/subscribe [:square i])
        emit (fn [_] (rf/dispatch [:square-clicked i]))] 
    [:button.square {:on-click emit}
                                 
                    v]))

(defn board []
  [:div
   [:div.board-row
    [square 0]
    [square 1]
    [square 2]]
   [:div.board-row
    [square 3]
    [square 4]
    [square 5]]
   [:div.board-row
    [square 6]
    [square 7]
    [square 8]]])

(defn ui []
  [:div.game
    [:div.game-board
      [board]]])

(defonce app-root
  (rdc/create-root (js/document.getElementById "app")))

(defn mount-ui
  []
  (rdc/render app-root [ui])) ;; mount the application's ui

(defn run
  []
  (rf/dispatch-sync [:initialize])     ;; puts a value into application state
  (mount-ui))

(run)