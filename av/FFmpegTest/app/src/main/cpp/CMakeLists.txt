cmake_minimum_required(VERSION 3.10.2)
project("ffmpegtest")


add_library( # Sets the name of the library.
        native-lib
        SHARED
        ${CMAKE_CURRENT_SOURCE_DIR}/native-lib.c
        ${CMAKE_CURRENT_SOURCE_DIR}/ffmpeg/cmdutils.c
        ${CMAKE_CURRENT_SOURCE_DIR}/ffmpeg/ffmpeg.c
        ${CMAKE_CURRENT_SOURCE_DIR}/ffmpeg/ffmpeg_filter.c
        ${CMAKE_CURRENT_SOURCE_DIR}/ffmpeg/ffmpeg_opt.c
        ${CMAKE_CURRENT_SOURCE_DIR}/ffmpeg/ffprobe.c
        ${CMAKE_CURRENT_SOURCE_DIR}/ffmpeg/ffmpeg_hw.c
        )



add_library(ffmpeg_my SHARED IMPORTED)
set_target_properties(
        ffmpeg_my
        PROPERTIES
        IMPORTED_LOCATION
        ${CMAKE_CURRENT_SOURCE_DIR}/../ffmpeg/${ANDROID_ABI}/libffmpeg.so
)
include_directories(${CMAKE_CURRENT_SOURCE_DIR})
include_directories(${CMAKE_CURRENT_SOURCE_DIR}/include)
include_directories(${CMAKE_CURRENT_SOURCE_DIR}/ffmpeg)

find_library( # Sets the name of the path variable.
        log-lib
        log)


target_link_libraries( # Specifies the target library.
        native-lib
        ${log-lib}
        ffmpeg_my
        )