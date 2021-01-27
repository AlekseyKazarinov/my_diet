package com.mydiet.mydiet.domain.entity;

public enum Status {
    DRAFT,
    ACCEPTED,
    PUBLISHED;

    public static void throwIfDraft(NutritionProgram program) {
        throwIfStatusIsIn(program, DRAFT);
    }

    public static void throwIfStatusIsIn(NutritionProgram program, Status... statuses) {
        for (var status : statuses) {
            if (status.equals(program.getStatus())) {
                var message = String.format("Not acceptable status %s for Nutrition Program %s",
                        program.getStatus(),
                        program.getNumber());

                throw new IllegalStateException(message);
            }
        }
    }
}
