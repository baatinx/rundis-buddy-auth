(ns rundis-buddy-auth.util
  (:require [monger.util :refer [get-id]])
  (:import [org.bson.types ObjectId]))

(defn doc-object-id->str
  "Accepts a map"
  [doc]
  (if (contains? doc :_id)
    (let [id (get-id doc)
          hexa-string (str id)]
      (and (org.bson.types.ObjectId/isValid hexa-string) (assoc doc :_id hexa-string)))
    nil))

(defn docs-object-id->str
  "Accepts a vector of maps"
  [docs]
  (pmap #(doc-object-id->str %)
        docs))

(defn doc-any-key-object-id->str
  [doc key]
  (if (contains? doc key)
    (let [id (key doc)
          hexa-string (str id)]
      (and (org.bson.types.ObjectId/isValid hexa-string) (assoc doc key hexa-string)))
    nil))

(defn docs-any-key-object-id->str
  [docs key]
  (pmap #(doc-any-key-object-id->str % key)
        docs))