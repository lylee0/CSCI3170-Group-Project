# CSCI3170 Group Project
## Book Ordering System

### Group 6
- HUI Wang Chi 1155159410
- LEE Lai Yan 1155158772
- CHENG Chi Yin 1155160221

### Environment Setup
- Host machine: CSE linux1

### Path Setting
- Please put all the source codes and the driver (ojdbc7.jar) in the same directory
- cd "path of the source file"
- For example:
```shell
cd ./CSCI3170-Group-Project
```

### Compilation
```shell
javac -cp ".:./ojdbc7.jar" *.java
```

### Deployment
```shell
java -cp ".:./ojdbc7.jar" Main
```

### Division of Work
- HUI Wang Chi
  - `Main.java`
  - `SystemInterface.java`
- LEE Lai Yan
  - `CustomerInterface.java`
- CHENG Chi Yin
  - `BookstoreInterface.java`
