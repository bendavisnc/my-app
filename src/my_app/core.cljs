(ns my-app.core
  (:require 
   [my-app.view :as view]
   [reagent.dom.client :as reagent-dom]))


(def app (js/document.getElementById "app"))

(defn- mount-app []
  (reagent-dom/render  
   (reagent-dom/create-root app)
   [view/ui]))


(defn- init []
  (mount-app))

(defn on-figwheel-reload []
  (mount-app))

(.addEventListener js/document "DOMContentLoaded" init)