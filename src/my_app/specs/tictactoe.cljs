(ns my-app.specs.tictactoe
  (:require
   [clojure.spec.alpha :as s]
   [my-app.pieces.tictactoe :as pieces]))

(s/def ::x #{pieces/x})

(s/def ::o #{pieces/o})

(s/def ::maybe-piece (s/with-gen (s/or :x ::x
                                       :o ::o
                                       :empty nil?)
                       #(s/gen #{pieces/x pieces/o nil})))

(s/def ::squares (s/coll-of ::maybe-piece))

(s/def ::winner ::maybe-piece)

(s/def ::status string?)

(s/def ::history-item (s/keys :req-un [::squares]))

(s/def ::history (s/coll-of ::history-item))

(s/def ::component
  (s/keys :req-un [::squares
                   ::winner
                   ::status
                   ::history]))
