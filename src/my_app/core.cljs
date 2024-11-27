(ns my-app.core
    (:require 
      [reagent.dom.client :as rdc]
      [re-frame.core :as rf]))


(enable-console-print!)

(def x "✘")
(def o "⭕")

(defn ui
  []
  [:div (str x o)])

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