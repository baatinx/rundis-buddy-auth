(ns rundis-buddy-auth.service
  (:require [buddy.hashers :as hs]
            ;;[buddy.sign.jws :as jws]
            [buddy.sign.jwt :as jwt]
            [buddy.core.keys :as ks]
            [clj-time.core :as t]
            [clojure.java.io :as io]
            [rundis-buddy-auth.store :as store]))

(defn add-user!
  [user]
  (store/add-user! (update-in user [:password] #(hs/encrypt %))))
;; (add-user! {:username "Mustafa Basit" :password "my-password" :user-roles ["role1" "role2" "role3"]})

(defn auth-user [credentials]
  (let [user (store/find-user-by-username (:username credentials))
        unauthed [false {:message "Invalid username or password"}]]
    (if user
      (if (hs/check (:password credentials) (:password user))
        [true {:user (dissoc user :password)}]
        unauthed)
      unauthed)))
;; (auth-user {:username "Mustafa Basit" :password "my-password"})

(defn- pkey
  [auth-conf]
  (ks/private-key
   (io/resource (:privkey auth-conf))
   (:passphrase auth-conf)))
;; (pkey {:privkey "auth_privkey.pem" :passphrase "secret-key"})

(defn create-auth-token
  [auth-conf credentials]
  (let [[ok? res] (auth-user credentials)
        exp (-> (t/plus (t/now) (t/days 1)))]
    (if ok?
      [true {:token (jwt/sign res
                              (pkey auth-conf)
                              {:alg :rs256 :exp exp})}]
      [false res])))
;; (create-auth-token {:privkey "auth_privkey.pem" :passphrase "secret-key"}
                  ;;  {:username "Mustafa Basit" :password "my-password"})
;; => [true
;;     {:token
;;      "eyJhbGciOiJSUzI1NiJ9.eyJ1c2VyIjp7Il9pZCI6IjVmNWQwM2JkYzZkYTg1NGM3YmIyMGEyZSIsInVzZXJuYW1lIjoiTXVzdGFmYSBCYXNpdCJ9LCJleHAiOjE2MDAwODMzNTd9.krsZVsd1MUW-YBmZL7S2M0Fdf-28dS2v3NcPTzUfsr_TskLxH_a7vG6yCSEURMhwyEyLCi038gvakUQPZNhcNhK3aiYphsmyf78jJuyO7OsA3TYQSTNraEOT_zV_hST5WE_4rSVVOZmKanzDUyOmhjPyGcKbDOSo3u8yKIWWNSHowPi4bPLWC5JjEmarSY-6tu8WrgK-KyDQgyVxxKMe7ZrlYFh2QUcBSQu-W7hBfa06UghabOHhhJ2pt6pqty3wFYGRTIFntB68TFwzpfcLYYenNLvEtNRi2oMyR3npumB4Csk_PxD-cu80sXXhOQCDh_QrCGNFSPamYVbn6hLY0g"}]

;; (jwt/unsign
;;  "eyJhbGciOiJSUzI1NiJ9.eyJ1c2VyIjp7Il9pZCI6IjVmNWQwM2JkYzZkYTg1NGM3YmIyMGEyZSIsInVzZXJuYW1lIjoiTXVzdGFmYSBCYXNpdCJ9LCJleHAiOjE2MDAwOTQ1MDF9.PgANMxL9t3jbwUgSfkXrA_pYO1RUanAUWrsmG2CnwbU8lM8c69KsQrPf-SznxSR_bvS0LkCrqAdHuDkWczEOWnEueeNst64DsUczwwlMlfx1l-TMwo8JKP9I035IpEC8o_mLdr4ZmYMva8fxztSt9kNlZc4WIwymxAQeB_dVlxRvaeE6nC04DVgEwu7SRLXEAQE1OYYsekUuuSo2St5RnKaoRrHfTDgc4kP9zofZLSHI6-s1EbX1HqwlI-AIkECZDX0omBTt0HwxvJw4ITTDudhso9kiKNvVlx4DSQPza9173MF1ZmG2XF_mgss5tBreXE0ZG8pxIIKz_cisEmw3kA"
;;  (ks/public-key  (io/resource "auth_pubkey.pem"))
;;  {:alg :rs256})