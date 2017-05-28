(defproject bookstore-rest "0.1.0-SNAPSHOT"
  :description "REST service for books"
  :url "http://czasprogramistow.pl"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [compojure "1.5.1"]
                 [ring/ring-json "0.4.0"]
                 [ring/ring-defaults "0.2.1"]
                 [cheshire "5.7.1"]
                 [org.clojure/java.jdbc "0.6.1"]
                 [com.h2database/h2 "1.4.195"]
                 [lobos "1.0.0-beta1"]]
  :plugins [[lein-ring "0.9.7"]]
  :ring {:init    bookstore-rest.db/init
         :handler bookstore-rest.handler/app}
  :profiles
  {:dev {:dependencies [[ring/ring-mock "0.3.0"]]}})
