export LC_ALL=C
before="$(find src/main/resources -regex ".*\\.png" -print0 | du -bch --files0-from=- | grep total)"
find src/main/resources -regex ".*\\.png" -print0 | xargs -P 0 -0 -I {} zopflipng --lossy_8bit --lossy_transparent --filters=01234mepb -y --iterations=10 {} {}
echo Before:
echo "$before"
echo After:
find src/main/resources -regex ".*\\.png" -print0 | du -bch --files0-from=- | grep total
