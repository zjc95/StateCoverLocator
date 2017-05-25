from XGBoost_expr.main import *
from XGBoost_var.main import *
from clustering.cluster import *
from Utils.config import *

import os
import sys

if __name__ == '__main__':
    if len(sys.argv) != 3:
        print("Wrong argument number!")
        sys.exit(1)
    config = Configure(
        sys.argv[1],
        sys.argv[2],
        5,
        'model/',
        'input/',
        'output/',
        10
    )
    cluster = Cluster(config)
    var_encoder = cluster.cluster_var()

    xgvar = XGVar(config)
    xgvar.train_var(var_encoder, 11)

    xgexpr = XGExpr(config)
    xgexpr.train_expr(var_encoder, 6)
