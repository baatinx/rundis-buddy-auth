(ns rundis-buddy-auth.jwt
  (:require [buddy.sign.jwt :as jwt]))

(def token (jwt/sign {:userid 1} "secret"))
;; => #'rundis-buddy-auth.jwt/token

token
;; => "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyaWQiOjF9.kHj3dQt4bViKHDCg9AklavdUv0Bdk4ufWdHd-TzYoJY"


(jwt/unsign token "secret")
;; => {:userid 1}

(jwt/unsign token "secret123")
;; => Execution error (ExceptionInfo) at buddy.sign.jws/unsign (jws.clj:165).
;; => Message seems corrupt or manipulated.
