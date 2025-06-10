call "deleteClass.bat"

@echo off  
echo ----------------------------------------------------------------------------                                                                                                                                          
echo   ,ad8888ba,   88b           d88  88888888ba                            
echo  d8"'    `"8b  888b         d888  88      "8b                           
echo d8'            88`8b       d8'88  88      ,8P                           
echo 88             88 `8b     d8' 88  88aaaaaa8P'  ,adPPYYba,  8b       d8  
echo 88             88  `8b   d8'  88  88""""""'    ""     `Y8  `8b     d8'  
echo Y8,            88   `8b d8'   88  88           ,adPPPPP88   `8b   d8'   
echo  Y8a.    .a8P  88    `888'    88  88           88,    ,88    `8b,d8'    
echo   `"Y8888Y"'   88     `8'     88  88           `"8bbdP"Y8      Y88'     
echo                                                                d8'      
echo                                                               d8'       
echo CMPay by Code Monarch - GUVI Hackathon Project 
echo ----------------------------------------------------------------------------


javac Main.java
if %errorlevel% == 0 (
    java Main
) else (
    echo Compilation failed.
)
pause