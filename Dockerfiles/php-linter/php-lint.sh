#!/bin/bash

# Read CLI options
LINT_DIRS=()
LINT_EXTS=()
QUIET=false

function lintFile() {
  OUTPUT=$(php -l $1 2>&1)
  rc=$?

  if [[ $rc != 0 ]]; then # Non-zero exit code, print error and exit
    >&2 echo "$OUTPUT"
    exit $rc
  elif [[ $rc == 0 ]] && [[ $QUIET == false ]]; then # all ok
    echo "$OUTPUT"
  fi
}

while [[ $# -gt 0 ]]; do
  case $1 in
    -f|--file)
      shift
      LINT_FILE="$1"
      ;;
    -e|--ext)
      shift
      LINT_EXTS+=("$1")
      ;;
    -d|--dir)
      shift
      LINT_DIRS+=("$1")
      ;;
    -q|--quiet)
      QUIET=true
      ;;
    -h|--help)
        printf 'Usage: %s <options>\n' "$0"
        printf '\t%s\n' "-f,--file: Optional argument: inidividual file to lint (if specified, all other arguments (except -q) are ignored"
        printf '\t%s\n' "-e,--ext: Optional argument: extensions to lint"
        printf '\t%s\n' "-d,--dir: Optional argument: directories to lint"
        printf '\t%s\n' "-q,--quiet: Optional argument: Quiet mode, only print errors"
        printf '\t%s\n' "-h,--help: Prints this help message"
        exit 0
      ;;
    *)
      echo "Unrecognized option ${1}"
      shift
      ;;
  esac
  shift
done

if [[ -z ${LINT_FILE} ]]; then
    # If no dirs were provided, use CWD
    if [[ -z "$LINT_DIRS" ]]; then
      LINT_DIRS=($(pwd))
    fi

    # If no extenions were provided, use reasonable defaults
    if [[ -z "$LINT_EXTS" ]]; then
      LINT_EXTS=(".php" ".phtml")
    fi

    # Run PHP Lint on all provided files and directories
    for dir in "${LINT_DIRS[@]}"; do
      for ext in "${LINT_EXTS[@]}"; do
        echo "Scanning directory ${dir} for extension ${ext}"

        # Scan current dir and ext and lint them
        for f in $(find "${dir}" -type f -name "*${ext}"); do
            lintFile "${f}"
        done
      done
    done
else
    lintFile "${LINT_FILE}"
fi