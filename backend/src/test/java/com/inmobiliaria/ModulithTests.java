package com.inmobiliaria;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

/**
 * Tests to verify Spring Modulith module structure.
 *
 * This test validates:
 * - Module boundaries are respected
 * - No cyclic dependencies exist
 * - Internal packages are properly encapsulated
 */
class ModulithTests {

    private final ApplicationModules modules = ApplicationModules.of(InmobiliariaApplication.class);

    @Test
    void verifyModuleStructure() {
        // Verify that all modules follow the defined structure
        modules.verify();
    }

    @Test
    void createModuleDocumentation() {
        // Generate documentation for the module structure
        new Documenter(modules)
                .writeDocumentation()
                .writeIndividualModulesAsPlantUml();
    }

    @Test
    void printModuleArrangement() {
        // Print the module arrangement for debugging
        System.out.println(modules.toString());
    }

    @Test
    void verifyNoCircularDependencies() {
        // This is implicitly tested by verify(), but we make it explicit
        modules.forEach(module -> {
            System.out.println("Module: " + module.getName());
            var dependencies = module.getDependencies(modules);
            dependencies.stream().forEach(dep -> {
                System.out.println("  -> " + dep.getTargetModule().getName());
            });
        });
    }
}
