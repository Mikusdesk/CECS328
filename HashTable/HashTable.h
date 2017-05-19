#ifndef HASTHABLE_H
#define HASHTABLE_H
#include <functional>
#include <vector>
#include <cmath>
#include <iostream>
template <typename TKey, typename TValue>
class HashTable{
public:
    HashTable(int s, std::function<std::size_t (TKey)> func);
    ~HashTable();
    void insert(TKey key, TValue value);
    TValue find(TKey key);
    void remove(TKey key);

    bool containsKey(TKey key);
    int count();
    std::vector<TKey> keySet();
private:
    struct Entry{
        TKey key;
        TValue value;
        bool isDeleted;
        Entry(){
            this->key = "";
            this->value = 0;
            this->isDeleted = false;
        }
    };
    int getHashIndex(TKey key);
    int getHashIndex(TKey key, int failures);
    bool keyFoundAtIndex(TKey key, int index);
    int probe(int i);
    void resizeTable(int newSize);

    std::function<std::size_t (TKey)> hashFunction;
    std::vector<Entry*> table;
    int mCount;
};

template <typename TKey, typename TValue>
HashTable<TKey, TValue>::HashTable(int s, std::function<std::size_t (TKey)> func){
    resizeTable(s);
    hashFunction = func;
}

template <typename TKey, typename TValue>
HashTable<TKey, TValue>::~HashTable(){
}

template <typename TKey, typename TValue>
void HashTable<TKey, TValue>::insert(TKey key, TValue value){
    //first check if key exists in table
    int contains= containsKey(key);
    if(contains){
        table[getHashIndex(key)]->value = value;//update
        return;
    }
    if(((mCount*1.)/(table.capacity()*1.)) >= 0.8){
        resizeTable(table.capacity()*2);
    }
    int failures = 0;
    //checks to see if key exists in table
    while(true){
        int index = (hashFunction(key) + probe(failures))%table.size();
        //empty array
        if(table[index] == nullptr || table[index]->isDeleted || table[index]->key == ""){
            if(table[index] == nullptr){
                table[index] = new Entry();
            }
            table[index]->key = key;
            table[index]->value = value;
            break;
        }//same key
        failures++;
    }
    mCount++;
}

template <typename TKey, typename TValue>
TValue HashTable<TKey, TValue>::find(TKey key){
    int index = getHashIndex(key);
    if(index < 0){
        throw std::runtime_error("End of hashtable.");
    }
    return table[index]->value;
}

template <typename TKey, typename TValue>
int HashTable<TKey, TValue>::getHashIndex(TKey key){
    return getHashIndex(key, 0);
}

template <typename TKey, typename TValue>
int HashTable<TKey, TValue>::getHashIndex(TKey key, int failures){
    if(failures >= table.size()){
        return -1;
    }
    else{
        int index = (hashFunction(key) + probe(failures))%table.size();
        //empty entry, the key was never added
        if(table[index] == nullptr){
            if(table[index] == nullptr){
                table[index] = new Entry();
            }
            return -1;
        }
        else if(table[index]->key == key){
            return index;
        }
        else if(table[index]->isDeleted == false){
            return -1;
        }
        else{
            return getHashIndex(key, failures + 1);
        }
    }
}
template <typename TKey, typename TValue>
void HashTable<TKey, TValue>::remove(TKey key){
    int index = getHashIndex(key);
    if(index >= 0){
        delete table[index];
        table[index] = new Entry();
        table[index]->isDeleted == true;
        mCount--;
    }
}
template <typename TKey, typename TValue>
bool HashTable<TKey, TValue>::keyFoundAtIndex(TKey key, int index){
    return table[index]->key == key;
}
template <typename TKey, typename TValue>
bool HashTable<TKey, TValue>::containsKey(TKey key){
    return getHashIndex(key) >= 0;
}
template <typename TKey, typename TValue>
int HashTable<TKey, TValue>::probe(int i){
    return (i*i + i)/2;
}
template <typename TKey, typename TValue>
int HashTable<TKey, TValue>::count(){
    return mCount;
}
template <typename TKey, typename TValue>
std::vector<TKey> HashTable<TKey, TValue>::keySet(){
    std::vector<TKey> temp;
    for(unsigned int i = 0; i < table.capacity(); i++){
        if(table[i] != nullptr && table[i]->key != ""){
            temp.push_back(table[i]->key);
        }
    }
    return temp;
}
template <typename TKey, typename TValue>
void HashTable<TKey, TValue>::resizeTable(int newSize){
    mCount = 0;
    std::vector<Entry*> temp(table.capacity());
    for(unsigned int i = 0; i < table.capacity();i++){
        if(table[i] != nullptr){
            temp[i] = table[i];
        }
    }
    table.clear();
    int exponent = std::ceil(log2(newSize));
    int nSize = std::pow(2, exponent);
    table.resize(nSize);
    int a = 0;
    for(unsigned int i = 0; i < temp.capacity(); i++){
        if(temp[i] != nullptr && temp[i]->key != ""){
            if(temp[i]->key != ""){
                ++a;
                insert(temp[i]->key, temp[i]->value);
                delete temp[i];
            }
            else{//free up empty entries
                delete temp[i];
            }
        }
    }
}

#endif // HASHTABLE_H
