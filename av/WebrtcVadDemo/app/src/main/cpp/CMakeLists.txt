
cmake_minimum_required(VERSION 3.10.2)


project("webrtcvaddemo")
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -Wall -pedantic")

aux_source_directory(${CMAKE_CURRENT_SOURCE_DIR}/vad_src/ DIR_LIB_SRCS)

#add_definitions添加编译参数
#解决报错-dwebrtc_postx  -dwebrtc_android
add_definitions(-DWEBRTC_POSIX)
add_definitions(-DWEBRTC_ANDROID)

add_library( # Sets the name of the library.
             native-lib
             SHARED
        native-lib.cpp
        ${DIR_LIB_SRCS})

find_library( # Sets the name of the path variable.
              log-lib
              log )

target_link_libraries( # Specifies the target library.
                       native-lib
                       ${log-lib} )