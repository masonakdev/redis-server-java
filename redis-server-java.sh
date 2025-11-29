set -e
set -u

echo "[redis-server-java.sh] Starting local server script"
echo "[redis-server-java.sh] CWD: $(pwd)"

cd "$(dirname "$0")"

export JAVA_HOME="$HOME/.sdkman/candidates/java/current"
export PATH="$JAVA_HOME/bin:$PATH"

echo "[redis-server-java.sh] Using Java version:"
java -version

echo "[redis-server-java.sh] Building project (mvn -DskipTests package)..."
mvn -q -B -DskipTests package

JAR="target/redis-server-java.jar"

if [ ! -f "$JAR" ]; then
  echo "[redis-server-java.sh] ERROR: expected $JAR not found. Build may have failed."
  echo "[redis-server-java.sh] Contents of target/:"
  ls -la target || true
  exit 1
fi

echo "[redis-server-java.sh] Running: java -jar $JAR"
exec java -jar "$JAR" "$@"
