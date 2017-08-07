reg Query "HKLM\Hardware\Description\System\CentralProcessor\0" | find /i "x86" > NUL && set OS=x86 || set OS=x86_64
echo %OS%
start javaw -classpath ./DDT2.0.0-%OS%.jar org.ccs.sandbox.sqltool.ui.DataDictionaryToolUI