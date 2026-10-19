# my_script.py
import sys
from loguru import logger

path="log.txt"

logger.add(path ,
           format="<yellow>{time: MMMM D, YYYY - HH:mm:ss}</yellow> -- <green>{level}</green> -- <level>{message}</level> {extra}",
           level="DEBUG"
)

a = sys.argv[1]

match a :
    case "info" :
        with logger.contextualize(log=sys.argv[3]):
            logger.info(f"{sys.argv[2]}")
    case "success" :
        with logger.contextualize(log=sys.argv[3]):
            logger.success(f"{sys.argv[2]}")
    case "error" :
        with logger.contextualize(log=sys.argv[3]):
    	    logger.error(f"{sys.argv[2]}")

#if a == "info" :
#    with logger.contextualize(log=sys.argv[3]):
#    	logger.info(f"{sys.argv[2]}")
#elif a == "success" :
#    with logger.contextualize(log=sys.argv[3]):
#	    logger.success(f"{sys.argv[2]}")
#elif a == "error" :
#    with logger.contextualize(log=sys.argv[3]):
#	    logger.error(f"{sys.argv[2]}")


#print(f"Script name: {sys.argv[0]}") # [0] is always the anme of the script
#if len(sys.argv) > 1:
#    print(f"Argument 1: {sys.argv[1]}") # first argument
#    print(f"Argument 2: {sys.argv[2]}") # second argument
