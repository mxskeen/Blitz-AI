#!/bin/bash

# Script to bump version and create a git tag for release

if [ $# -eq 0 ]; then
    echo "Usage: $0 <version> (e.g., 1.0.1)"
    exit 1
fi

VERSION=$1
VERSION_CODE=$(date +%s)

echo "Bumping version to $VERSION (code: $VERSION_CODE)"

# Update version in app/build.gradle
sed -i "s/versionCode .*/versionCode $VERSION_CODE/" app/build.gradle
sed -i "s/versionName .*/versionName \"$VERSION\"/" app/build.gradle

echo "Updated app/build.gradle with version $VERSION and code $VERSION_CODE"

# Commit changes
git add app/build.gradle
git commit -m "Bump version to $VERSION"

# Create and push tag
if git tag "v$VERSION" 2>/dev/null; then
    echo "Created new tag v$VERSION"
    git push origin "v$VERSION"
    git push origin main
    echo "Pushed tag v$VERSION and main branch"
else
    echo "Tag v$VERSION already exists, deleting and recreating..."
    git tag -d "v$VERSION" 2>/dev/null || true
    git push origin --delete "v$VERSION" 2>/dev/null || true
    git tag "v$VERSION"
    git push origin "v$VERSION"
    git push origin main
    echo "Recreated and pushed tag v$VERSION"
fi
echo "GitHub Actions will now build and release the APKs automatically."
