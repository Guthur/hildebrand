(ns hildebrand.response-test
  (:require [hildebrand.response :refer :all]
            [clojure.test :refer :all]))

(deftest create-table+
  (is (= {:table :users
          :attrs {:name :string :age :number}
          :keys [:name :age]
          :throughput {:read 5 :write 1}
          :indexes
          {:global [{:name :gs-index
                     :keys [:age :something]
                     :project [:include [:something-else]]
                     :throughput {:read 1 :write 1}}]}}
         (restructure-response
          :create-table
          {:table-description
           {:table-name :users
            :provisioned-throughput {:read 5 :write 1}
            :key-schema [{:attribute-name :age  :key-type :range}
                         {:attribute-name :name :key-type :hash}]
            :attribute-definitions
            [{:attribute-name :name :attribute-type :S}
             {:attribute-name :age  :attribute-type :N}]
            :global-secondary-indexes
            [{:index-name :gs-index
              :key-schema [{:attribute-name :age :key-type :hash}
                           {:attribute-name :something :key-type :range}]
              :projection {:projection-type :include 
                           :non-key-attributes [:something-else]}
              :provisioned-throughput {:read 1 :write 1}}]}}))))