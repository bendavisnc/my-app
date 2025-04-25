(ns my-app.specs.tictactoe
  (:require
   [clojure.spec.alpha :as s]
   [my-app.pieces.tictactoe :as pieces]))


(s/def ::x #{pieces/x})

(s/def ::o #{pieces/o})

(s/def ::square (s/with-gen
                   (s/or :x ::x
                         :o ::o
                         :empty nil?)
                   #(s/gen #{pieces/x pieces/o nil})))

(s/def ::squares (s/coll-of ::square))

(s/def ::component
  (s/keys :req-un [::squares]))
