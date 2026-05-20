#!/bin/bash

echo "Installing CCMS CLI..."

# Create install directory
mkdir -p ~/.ccms/bin

# Download latest JAR
curl -L https://github.com/ReebanAustrive/AI-Assitant-Micro-Saas/releases/latest/download/ccms.jar \
     -o ~/.ccms/bin/ccms.jar

# Create wrapper script
cat > ~/.ccms/bin/ccms << 'EOF'
#!/bin/bash
java -jar ~/.ccms/bin/ccms.jar "$@"
EOF

# Make executable
chmod +x ~/.ccms/bin/ccms

# Add to PATH
if ! grep -q 'ccms/bin' ~/.bashrc; then
    echo 'export PATH="$HOME/.ccms/bin:$PATH"' >> ~/.bashrc
fi

if ! grep -q 'ccms/bin' ~/.zshrc 2>/dev/null; then
    echo 'export PATH="$HOME/.ccms/bin:$PATH"' >> ~/.zshrc 2>/dev/null
fi

echo "✅ CCMS CLI installed successfully!"
echo "Run: source ~/.bashrc"
echo "Then: ccms init --repo <url> --arch <path>"