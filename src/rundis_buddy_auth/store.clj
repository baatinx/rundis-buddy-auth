(ns rundis-buddy-auth.store
(:require [monger.core :as mg]
          [monger.collection :as mc]
          [monger.util :refer [object-id get-id]]
          [rundis-buddy-auth.util :refer [doc-object-id->str docs-object-id->str]]))

(def ^:private host "localhost")
(def ^:private port 27017)
(def ^:private db-name "rundisAcmeBuddyDB")

(defn ^:private get-conn
  []
  (mg/connect {:host host
               :port port}))

(defn ^:private get-db-ref
  []
  (mg/get-db (get-conn) db-name))

(def db (get-db-ref))

(defn insert-doc
  "Accepts a map"
  [coll doc]
  (mc/insert db coll doc))

(defn insert-doc-and-return
  "Accepts a map"
  [coll doc]
  (mc/insert-and-return db coll doc))

(defn insert-docs
  "Accepts a vector of maps"
  [coll docs]
  (mc/insert-batch db coll docs))

(defn retrieve-docs
  "Keep it simple and short - KISS"
  [coll]
  (let [coll coll
        db db]
    (-> (mc/find-maps db coll)
        docs-object-id->str)))

(defn retrieve-doc-by-id
  [coll id]
  (let [coll coll]
    (if (org.bson.types.ObjectId/isValid id)
      (->> (object-id id)
           (mc/find-map-by-id db coll)
           doc-object-id->str)
      nil)))

(defn retrieve-doc-by-ref
  [coll ref]
  (let [coll coll
        db db]
    (-> (mc/find-one-as-map db coll ref)
        doc-object-id->str)))

(defn reset-db-state
  []
  (mg/drop-db (get-conn) db-name))

(defn add-user! [user]
  (let [doc {:_id (object-id)
             :username (:username user)
             :password (:password user)}
        user-id (-> (insert-doc-and-return "user" doc)
                    get-id)]
    (doseq [user-roles (:user-roles user)]
      (let [doc {:user-id user-id
                 :user-roles user-roles}]
        (insert-doc "userRoles" doc)))))
;; (add-user! {:username "Mustafa Basit" :password "my-password" :user-roles ["role1" "role2" "role3"]})

(defn find-user-by-username
  "*** pending - user role support"
  [username]
  (when-let [user (retrieve-doc-by-ref "user" {:username username})]
    user))
;; (find-user-by-username "Mustafa Basit")















(comment
(defn- find-user-roles [user-id]
  (map (fn [row] {:role-id (:id row) :application-id (:application_id row)})
       (jdbc/query conn ["select r.id, r.application_id
                         from role r
                         inner join user_role ur on r.id = ur.role_id
                         where ur.user_id = ?" user-id])))

(defn find-user-by-id [conn id]
  (when-let [user
             (first
              (jdbc/query conn ["select * from user where id = ?" id]))]
    (assoc user :user-roles (find-user-roles conn (:id user)))))

(defn add-refresh-token! [conn params]
  (jdbc/insert! conn :refresh_token params))

(defn invalidate-token!
  ([conn id]
   (jdbc/update! conn :refresh_token {:valid false} ["id = ?" id]))
  ([conn user-id issued]
   (jdbc/update! conn :refresh_token {:valid false} ["user_id = ? and issued = ?" user-id issued])))

(defn find-token-by-unq-key [conn user-id issued]
  (first
   (jdbc/query conn ["select * from refresh_token where user_id = ? and issued = ?" user-id issued])))



)