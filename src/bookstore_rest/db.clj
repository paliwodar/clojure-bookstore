(ns bookstore-rest.db
  (:require [clojure.java.jdbc :as jdbc]))


(def db-spec {:classname "org.h2.Driver" :subprotocol "h2" :subname "mem:bookstore;DB_CLOSE_DELAY=-1"})

(defn init []
  ;(jdbc/execute! db-spec ["DROP TABLE BOOKS"])
  (jdbc/db-do-commands db-spec (jdbc/create-table-ddl :books
                                                      [[:id "varchar(256)" "primary key"]
                                                       [:author "varchar(32)"]
                                                       [:title "varchar(64)"]
                                                       [:year "integer"]])))

(defn read-books []
  (jdbc/query db-spec ["SELECT * FROM books"]))

(defn insert-book [body]
  (jdbc/insert! db-spec :books body))

(defn read-book [id]
  (jdbc/query db-spec ["SELECT * FROM books where id = ?", id]))

(defn delete-book [id]
  (jdbc/execute! db-spec ["DELETE FROM books where id = ?", id]))

(defn update-book [id body]
  (jdbc/update! db-spec :books body ["id  = ?" id]))