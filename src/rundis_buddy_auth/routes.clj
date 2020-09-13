(ns rundis-buddy-auth.routes
  (:require [compojure.core :refer [defroutes POST]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.json :refer [wrap-json-response wrap-json-params]]
            [rundis-buddy-auth.handlers :as handlers]))

(defroutes app-routes
  (POST "/create-auth-token" [] handlers/create-auth-token))

(defn wrap-config [handler]
  (fn [req]
    (handler (assoc req :auth-conf {:privkey "auth_privkey.pem"
                                    :passphrase "secret-key"}))))

(def app
  (-> app-routes
      wrap-config
      wrap-keyword-params
      wrap-json-params
      wrap-json-response))

;; open terminal
;; curl -i -X POST -d '{"username": "Mustafa Basit", "password":"my-password"}' -H "Content-type: application/json" http://localhost:8000/create-auth-token
;; => {"token":"eyJhbGciOiJSUzI1NiJ9.eyJ1c2VyIjp7Il9pZCI6IjVmNWQwM2JkYzZkYTg1NGM3YmIyMGEyZSIsInVzZXJuYW1lIjoiTXVzdGFmYSBCYXNpdCJ9LCJleHAiOjE2MDAwOTQ1MDF9.PgANMxL9t3jbwUgSfkXrA_pYO1RUanAUWrsmG2CnwbU8lM8c69KsQrPf-SznxSR_bvS0LkCrqAdHuDkWczEOWnEueeNst64DsUczwwlMlfx1l-TMwo8JKP9I035IpEC8o_mLdr4ZmYMva8fxztSt9kNlZc4WIwymxAQeB_dVlxRvaeE6nC04DVgEwu7SRLXEAQE1OYYsekUuuSo2St5RnKaoRrHfTDgc4kP9zofZLSHI6-s1EbX1HqwlI-AIkECZDX0omBTt0HwxvJw4ITTDudhso9kiKNvVlx4DSQPza9173MF1ZmG2XF_mgss5tBreXE0ZG8pxIIKz_cisEmw3kA"}