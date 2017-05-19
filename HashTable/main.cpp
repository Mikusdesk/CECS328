#include "hashtable.h"
#include <string>
#include <fstream>
#include <iostream>
void mainFunct(HashTable<std::string, int> &table);
void test(HashTable<std::string, int> &table);
int main(int argc, char** argv){
    std::string fileName = "players_homeruns.csv";
    std::ifstream reader(fileName);
    std::string name;
    int homeruns = 0;
    HashTable<std::string, int> table(256, std::hash<std::string>{});
    std::string newLineFeed;
    while(std::getline(reader, name, ',')){
        //get data as int
        reader >> homeruns;
        table.insert(name, homeruns);
        //consume '\n'
        std::getline(reader, newLineFeed);
    }
    mainFunct(table);
    return 0;
}
void mainFunct(HashTable<std::string, int> &table){
    std::string n;
    int hr = 0;
    while(true){
        std::cout << "Please enter a name or type 'exit' to quit.\n";
        getline(std::cin, n);
        if(n == "exit"){
            break;
        }
        try{
            hr = table.find(n);
            std::cout << "Homeruns: " << hr << std::endl;
        }catch(std::runtime_error e){
            std::cout << e.what() << "\nKey does not exist in table.\n";
        }
    }
}

void test(HashTable<std::string, int> &table){
    int a = 0;
    while(true){
        std::string n;
        if(a == 11){
            break;
        }
        std::cout << "\n1Find"
                << "\n2Count"
                << "\n3Remove"
                << "\n4KeySet"
                << "\n5ContainsKey\n";
        std::cout << "Select a menu\n";
        std::cin >> a;
        std::cin.ignore();
        if(a == 1){
            std::cout << "Enter a key to find: ";
            getline(std::cin, n);
            try{
                std::cout << "Find: " << table.find(n);
            }catch(std::runtime_error e){
                std::cout << e.what() << "\nNot Found.\n";
            }
        }
        else if(a == 2){
            std::cout << "Count: " << table.count();
        }
        else if(a == 3){
            std::cout << "Enter a key to remove: ";
            getline(std::cin, n);
            table.remove(n);
        }
        else if(a == 4){
            std::vector<std::string> v = table.keySet();
            for(int x = 0; x < v.capacity(); x++){
                std::cout << v[x] << std::endl;
            }
        }
        else if(a == 5){
            std::cout << "Enter a key to see if it is in: ";
            getline(std::cin, n);
            if(table.containsKey(n)){
                std::cout << "True\n";
            }
            else{
                std::cout << "False\n";
            }
        }
    }
}
