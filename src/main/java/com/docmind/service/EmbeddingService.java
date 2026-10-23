
package com.docmind.service;

import ai.djl.ModelException;
import ai.djl.huggingface.translator.TextEmbeddingTranslatorFactory;
import ai.djl.inference.Predictor;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;
import ai.djl.translate.TranslateException;

import java.io.IOException;

public class EmbeddingService implements AutoCloseable {

    private final ZooModel<String, float[]> model;

    public EmbeddingService() throws IOException, ModelException {

        Criteria<String, float[]> criteria = Criteria.builder()
                .setTypes(String.class, float[].class)
                .optModelUrls(
                    "djl://ai.djl.huggingface.pytorch/sentence-transformers/all-MiniLM-L6-v2"
                )
                .optEngine("PyTorch")
                .optTranslatorFactory(new TextEmbeddingTranslatorFactory())
                .optProgress(new ProgressBar())
                .build();

        this.model = criteria.loadModel();
    }

    public float[] generateEmbedding(String text)
            throws TranslateException {

        try (Predictor<String, float[]> predictor = model.newPredictor()) {
            return predictor.predict(text);
        }
    }

    @Override
    public void close() {
        model.close();
    }
}
