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
                            nil))
       :is-x-next? true})))

(rf/reg-event-db
  :square-clicked            
  (fn [db [_ i]]

    (let [squares (:squares db)
          is-x-next? (:is-x-next? db)
          square-occupied? (squares i)
          should-update-state? (not square-occupied?)]
      (println ["event handler", db, i])
      (if (not should-update-state?)
        db
        ;; else
        (-> db
            (update :squares
                    assoc
                    i
                    (if is-x-next? x o))
            (update :is-x-next? not))))))
                

(rf/reg-sub
  :square
  (fn [db [_, i]]
    (println ["subscription handler", db, i])
    (get-in db [:squares i])))

(rf/reg-sub
  :status
  (fn [db [_, i]]
    (str "todo, status")))

(rf/reg-sub
  :history
  (fn [db [_, i]]
    []))

;; ui components

(defn square [i]
  (let [_ (println "@square")
        v @(rf/subscribe [:square i])
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

(defn jump-to-button [move-index, desc]
  [:button {:on-click (fn []
                        (println "beans"))}
    desc])

(defn game-info []
  (let [history @(rf/subscribe [:history])
        status @(rf/subscribe [:status])]
    [:div.game-info [:div status]
                    [:ol (for [i (range (count history))
                               :let [desc (if (zero? i)
                                            "Go to game start"
                                            (str "Go to move #" i))]]
                           [:li {:key i} 
                               [jump-to-button i desc]])]]))

(defn ui []
  [:div.game
    [:div.game-board
      [board]]
    [game-info]])

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