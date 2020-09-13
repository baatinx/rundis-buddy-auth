(ns rundis-buddy-auth.handlers
  (:require [rundis-buddy-auth.service :as service]))

(defn create-auth-token
  [req]
  (let [[ok? res] (service/create-auth-token (:auth-conf req)
                                             (:params req))]
    (if ok?
      {:status 201 :body res}
      {:status 401 :body res})))