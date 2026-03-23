@echo off

REM === このバッチがある場所（= pom.xml の場所）に移動 ===
cd /d "%~dp0"

echo ================================
echo Spring Java Format 適用開始
echo ================================
call mvn spring-javaformat:apply
if %ERRORLEVEL% neq 0 (
  echo [ERROR] spring-javaformat:apply に失敗しました
  pause
  exit /b 1
)

echo.
echo ================================
echo Maven Clean 開始
echo ================================
call mvn clean
if %ERRORLEVEL% neq 0 (
  echo [WARN] mvn clean に失敗しました。target を強制削除して再試行します...

  REM ロックしている可能性のある Java を停止（あってもなくてもOK）
  taskkill /IM java.exe /F 2>nul
  taskkill /IM javaw.exe /F 2>nul

  REM ★★★ フルパスなしで削除（相対パス）★★★
  rmdir /s /q "target" 2>nul

  echo [INFO] 再度 mvn clean を実行します...
  call mvn clean
  if %ERRORLEVEL% neq 0 (
    echo [ERROR] mvn clean が再試行でも失敗しました
    echo        Eclipseを閉じてからもう一度実行してください。
    pause
    exit /b 1
  )
)

echo.
echo ==========================================
echo Maven Install（テストスキップ）開始
echo ==========================================
call mvn -DskipTests install
if %ERRORLEVEL% neq 0 (
  echo [ERROR] mvn install に失敗しました
  pause
  exit /b 1
)

echo.
echo ====== 全工程 正常完了 ======
pause
