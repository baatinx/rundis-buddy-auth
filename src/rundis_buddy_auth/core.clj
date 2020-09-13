(ns rundis-buddy-auth.core
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [rundis-buddy-auth.routes :refer [app]]))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (run-jetty app {:port 8000
                  :join false})
  (println "Server Running..."))