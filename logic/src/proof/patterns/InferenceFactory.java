package proof.patterns;

import proof.Inference;

public interface InferenceFactory<T> {
    public Inference newInference(T engine);
    public Inference newInference(T engine, T from);
}
