library(ggplot2)
library(data.table)
results = data.table(read.csv("C:\\hack\\recsys\\recsys-code-eval-assignment\\eval-assignment\\target\\analysis\\eval-results.csv"))
ggplot(results[,list(RMSE=mean(RMSE.ByUser)),by=list(Algorithm,Partition)]) +
    aes(x=Algorithm, y=RMSE) +
    geom_boxplot(position="dodge")