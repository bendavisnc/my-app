#!/bin/bash

# Get the script's filename without the path
script_name=$(basename "$0")

# Remove the .sh extension if it exists
script_name_no_ext="${script_name%.sh}"

# Replace underscores with spaces
new_command="${script_name_no_ext//_/ }"

# Echo the new command for verification
echo "Executing: $new_command"

# Execute the new command
eval "$new_command"
