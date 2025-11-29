set -e
set -u

echo "[test-redis-server-java.sh] Starting local server script"
echo "[test-redis-server-java.sh] CWD: $(pwd)"

cd "$(dirname "$0")"

export JAVA_HOME="$HOME/.sdkman/candidates/java/current"
export PATH="$JAVA_HOME/bin:$PATH"

echo "[test-redis-server-java.sh] Using Java version:"
java -version

echo "[test-redis-server-java.sh] Building project (mvn -DskipTests package)..."
mvn -q -B -DskipTests package

JAR="target/redis-server-java.jar"

if [ ! -f "$JAR" ]; then
  echo "[test-redis-server-java.sh] ERROR: expected $JAR not found. Build may have failed."
  echo "[test-redis-server-java.sh] Contents of target/:"
  ls -la target || true
  exit 1
fi
