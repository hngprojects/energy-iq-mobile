#!/usr/bin/env bash
# Rename the project package from dev.logickoder.starter to a new package.
# Usage: ./rename.sh com.yourcompany.appname
set -e

OLD_PACKAGE="dev.logickoder.starter"
NEW_PACKAGE="$1"

if [ -z "$NEW_PACKAGE" ]; then
    echo "Usage: ./rename.sh <new.package.name>"
    echo "Example: ./rename.sh com.hng.myapp"
    exit 1
fi

echo "Renaming $OLD_PACKAGE → $NEW_PACKAGE ..."

# Replace in Kotlin source files
find . \( -name "*.kt" \) \
    -not -path "*/.git/*" \
    -not -path "*/build/*" \
    -not -path "*/.gradle/*" \
    | xargs sed -i '' "s|${OLD_PACKAGE}|${NEW_PACKAGE}|g"

# Replace in Gradle files
find . \( -name "*.gradle.kts" -o -name "*.gradle" \) \
    -not -path "*/.git/*" \
    -not -path "*/build/*" \
    | xargs sed -i '' "s|${OLD_PACKAGE}|${NEW_PACKAGE}|g"

# Replace in XML files (AndroidManifest, etc.)
find . -name "*.xml" \
    -not -path "*/.git/*" \
    -not -path "*/build/*" \
    | xargs sed -i '' "s|${OLD_PACKAGE}|${NEW_PACKAGE}|g"

# Replace in Swift files (iOS ContentView)
find . -name "*.swift" \
    -not -path "*/.git/*" \
    | xargs sed -i '' "s|${OLD_PACKAGE}|${NEW_PACKAGE}|g" 2>/dev/null || true

echo "Done."
echo ""
echo "Next steps:"
echo "  1. Update applicationId in androidApp/build.gradle.kts if not done automatically"
echo "  2. Set BASE_URL in composeApp/src/commonMain/.../core/network/NetworkConfig.kt"
echo "  3. Sync Gradle (./gradlew :androidApp:assembleDebug)"
echo "  4. Update app_name in androidApp/src/main/res/values/strings.xml"
