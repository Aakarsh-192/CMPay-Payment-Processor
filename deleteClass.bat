@echo off
echo Deleting all .class files in and under %cd% ...
for /R %%f in (*.class) do (
    del "%%f"
)
echo Done.
CLS
echo deleted old class files