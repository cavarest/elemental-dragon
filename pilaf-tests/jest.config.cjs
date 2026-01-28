module.exports = {
  testEnvironment: 'node',
  testMatch: ['**/stories/**/*.test.js'],
  testTimeout: 120000, // 2 minutes per test

  // Verbose output in CI
  verbose: process.env.CI === 'true',

  // Module paths
  moduleDirectories: ['node_modules'],

  // Test file extensions - include json for prismarine-physics features.json
  moduleFileExtensions: ['js', 'json'],

  // Run tests serially to avoid connection throttling
  maxWorkers: 1,

  // Coverage configuration
  collectCoverageFrom: [
    'stories/**/*.js',
    '!stories/**/*.test.js'
  ],
  coverageThreshold: null,

  // JUnit XML reporter for CI
  reporters: [
    'default',
    [
      'jest-junit',
      {
        outputDirectory: 'test-results',
        outputName: 'junit.xml',
        ancestorSeparator: ' › ',
        uniqueOutputName: false,
        suiteNameTemplate: '{filepath}',
        classNameTemplate: '{classname}',
        titleTemplate: '{title}',
        addFileAttribute: true
      }
    ]
  ]
};
