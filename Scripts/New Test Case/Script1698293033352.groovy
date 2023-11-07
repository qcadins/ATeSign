function calculateSum(a, b) {
  // Deliberate error: Missing semicolon
  return a + b
}

console.log("The sum is: " + calculateSum(3, 5))

// Deliberate error: Using undeclared variable
console.log("The product is: " + product(2, 4))

// Deliberate error: Missing 'else' in the if statement
if (true) {
  console.log("This is true")
}
