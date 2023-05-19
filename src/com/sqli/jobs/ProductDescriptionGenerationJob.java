package com.sqli.jobs;

import com.sqli.model.ProductDescriptionGenerationCronJobModel;
import com.sqli.service.ProductDescriptionService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.List;

public class ProductDescriptionGenerationJob extends AbstractJobPerformable<ProductDescriptionGenerationCronJobModel> {

    private ProductDescriptionService descriptionService;

    // Add a setter for Spring to inject the service
    public void setDescriptionService(ProductDescriptionService descriptionService) {
        this.descriptionService = descriptionService;
    }

    /**
     * Performs the product description generation cron job.
     * Retrieves products without a description and updates their descriptions.
     * If an error occurs during the update, an error message is printed along with the stack trace.
     *
     * @param cronJobModel The cron job model representing the product description generation cron job.
     * @return The result of the cron job execution.
     */
    @Override
    public PerformResult perform(final ProductDescriptionGenerationCronJobModel cronJobModel) {

        List<ProductModel> products = getProductsWithoutDescription();

        products.forEach(product -> {
            try {
                updateProductDescription(product);
            } catch (Exception e) {
                System.out.println("Error generating description for product " + product.getName());
                e.printStackTrace();
            }
        });

        return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
    }

    /**
     * Retrieves a list of products without a description.
     *
     * @return The list of products without a description.
     */
    private List<ProductModel> getProductsWithoutDescription() {
        String queryStr = "SELECT {p:" + ProductModel.PK + "} " +
                "FROM {" + ProductModel._TYPECODE + " AS p} " +
                "WHERE {p:" + ProductModel.DESCRIPTION + "} IS NULL OR {p:" + ProductModel.DESCRIPTION + "} = ''" +
                "AND {p:" + ProductModel.NAME + "} IS NOT NULL";

        FlexibleSearchQuery query = new FlexibleSearchQuery(queryStr);
        query.setCount(1); // Limit the results to 1
        SearchResult<ProductModel> result = flexibleSearchService.search(query);

        return result.getResult();
    }

    /**
     * Updates the description of the given product.
     *
     * @param product The product to update.
     * @throws Exception If an error occurs during the update.
     */
    private void updateProductDescription(ProductModel product) throws Exception {
        String name = product.getName();
        String features = product.getFeatures().toString();

        String description = descriptionService.generateProductDescription(name, features);
        product.setDescription(description);
        modelService.save(product);

    }

}
