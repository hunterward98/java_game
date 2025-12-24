#!/bin/bash

# Coverage Analysis Script
# Generates a quick reference of testable vs non-testable classes

echo "=== Test Coverage Analysis ==="
echo ""

# Parse the JaCoCo HTML report to extract package coverage
REPORT_DIR="core/build/reports/jacoco/test/html"

if [ ! -d "$REPORT_DIR" ]; then
    echo "Error: Coverage report not found. Run './gradlew test jacocoTestReport' first."
    exit 1
fi

# Extract overall coverage
OVERALL=$(grep -oP 'Total.*?<td class="ctr2" id="c.*?">\K\d+(?=%</td>)' "$REPORT_DIR/index.html" | head -1)
echo "Overall Coverage: ${OVERALL}%"
echo "Target: 40%"
echo "Remaining: $((40 - OVERALL))%"
echo ""

# Function to get package coverage
get_package_coverage() {
    local package=$1
    local html_file="$REPORT_DIR/$package/index.html"

    if [ -f "$html_file" ]; then
        # Get total coverage percentage for the package
        grep -oP '<tfoot>.*?<td class="ctr2".*?>\K\d+(?=%</td>)' "$html_file" | head -1
    else
        echo "N/A"
    fi
}

# Function to list classes with 0% coverage in a package
list_untested_classes() {
    local package=$1
    local html_file="$REPORT_DIR/$package/index.html"

    if [ -f "$html_file" ]; then
        # Extract class names with 0% coverage
        grep -oP '<a href=".*?\.html" class="el_class">\K[^<]+(?=</a>)' "$html_file" | while read class; do
            # Check if this class row has 0% coverage
            local line=$(grep -A 20 "class=\"el_class\">$class</a>" "$html_file" | grep -oP 'ctr2.*?>\K\d+(?=%</td>)' | head -1)
            if [ "$line" = "0" ]; then
                echo "  - $class (0%)"
            fi
        done
    fi
}

echo "=== Package Coverage Breakdown ==="
echo ""

# List all packages with coverage
echo "High Priority (Low Coverage, Likely Testable):"
echo "  loot: $(get_package_coverage 'io.github.inherit_this.loot')%"
list_untested_classes "io.github.inherit_this.loot"
echo ""

echo "  entities: $(get_package_coverage 'io.github.inherit_this.entities')%"
list_untested_classes "io.github.inherit_this.entities"
echo ""

echo "  util: $(get_package_coverage 'io.github.inherit_this.util')%"
list_untested_classes "io.github.inherit_this.util"
echo ""

echo "  audio: $(get_package_coverage 'io.github.inherit_this.audio')%"
list_untested_classes "io.github.inherit_this.audio"
echo ""

echo "Medium Priority:"
echo "  particles: $(get_package_coverage 'io.github.inherit_this.particles')%"
list_untested_classes "io.github.inherit_this.particles"
echo ""

echo "  save: $(get_package_coverage 'io.github.inherit_this.save')%"
list_untested_classes "io.github.inherit_this.save"
echo ""

echo "  dungeon: $(get_package_coverage 'io.github.inherit_this.dungeon')%"
list_untested_classes "io.github.inherit_this.dungeon"
echo ""

echo "Low Priority (LibGDX Dependencies):"
echo "  world: $(get_package_coverage 'io.github.inherit_this.world')%"
echo "  rendering: $(get_package_coverage 'io.github.inherit_this.rendering')%"
echo ""

echo "=== Quick Wins (Simple Enums/Data Classes) ==="
echo ""
echo "Enums to test:"
find core/src/main/java -name "*.java" -exec grep -l "^public enum" {} \; | while read file; do
    classname=$(basename "$file" .java)
    package=$(echo "$file" | sed 's|core/src/main/java/||' | sed 's|/|.|g' | sed "s|.$classname.java||")

    # Check if test exists
    testfile="core/src/test/java/$(echo $package | sed 's|\.|/|g')/${classname}Test.java"
    if [ ! -f "$testfile" ]; then
        echo "  ✗ $package.$classname (NO TEST)"
    else
        echo "  ✓ $package.$classname (tested)"
    fi
done

echo ""
echo "=== Recommendations ==="
echo "1. Focus on enums without tests (marked with ✗ above)"
echo "2. Test data classes in high-priority packages"
echo "3. Avoid classes with LibGDX dependencies (Texture, FileHandle, etc.)"
echo ""
